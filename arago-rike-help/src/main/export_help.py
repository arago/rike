#!/usr/bin/python
# -*- coding: utf-8 -*-

from sys import argv
from sys import exit
from os import mkdir
import urllib,urllib2
import httplib2
import StringIO

######################################

def get_resources(wiki_url, resources):    
    helpPages = findHelpPages(wiki_url+"_pages", "help.rike")
    for pageName in helpPages:
        page = readFullPage(wiki_url + pageName)        
        page = extractBody(page)
        resources.append(('html', pageName, page))
        extractImgs(page, resources)


def findHelpPages(wiki_url, name_prefix):
    content = readFullPage(wiki_url)
    content = content[content.find("markdown-body"):]
    content = content[:content.find("</div>")]
    
    subpages = []
    while 1:
        pos = content.find(">help.rike.")
        if pos == -1:
            break
        content = content[pos+1:]
        pos = content.find("</a>")         
        subpages.append(content[:pos])
        content = content[pos:]
        
    return subpages
 
 
def readFullPage(url):
    http = httplib2.Http()
    headers = {'Content-type': 'application/x-www-form-urlencoded'}
    data = ""
    try:
        req = urllib2.Request(url, None, headers)
        usock = urllib2.urlopen(req)
        data = usock.read()
        usock.close()                   
    except Exception as ex:
        print ex
    return data


def extractBody(page):
    page = page[page.find('class="markdown-body"')+24:]
    totalSize=0
    divCounter = 1
    rob = page
    while divCounter > 0:
        ind = rob.find("div>")
        if ind < 0 or divCounter == 0:
            break
        if rob[ind-1] == '/' :
            divCounter -= 1
        else:
            divCounter += 1
        totalSize += ind
        rob = rob[ind +1:]
    
    return page[:totalSize-3]
       

def extractImgs(page, resources):
    rob = page
    while 1:
        ind = rob.find('<img src=')
        if ind < 0:
            break
        rob = rob[ind+10:]
        ind = rob.find('"')
        imgURL = rob[:ind]
        rob = rob[ind:]
        resources.append(('img', imgURL, None))

def replace_links(content):
    pos = content.find('<a href="help.')
    if pos < 0:
        return content
    end = content.find('"',pos + 13);
    return content[:pos] + '<a href="javascript:void(0);" onclick="return de.arago.help.Provider.show(\'' + content[pos+14:end] + '\');' + replace_links(content[end:])
            
#use template index.html and resources to create new contents
def save_resources(dest_path, tmpl_dir, resources):
    template = getTemplate(tmpl_dir)

    for r in resources:
        if r[0] == 'html':
            newpage = template            
            newpage = newpage.replace("%title%", r[1])
            content = r[2].replace('https://raw.github.com/arago/rike/master/arago-rike-help/src/main/webapp/help/','/arago-rike-help/help/')
            newpage = newpage.replace("%content%", replace_links(content))
            fout = open(dest_path + '/' + r[1] + '.html','w')
            fout.write(newpage)
            fout.close()
        elif r[0] == 'img':
            writeImg(dest_path, r[1])


def getTemplate(tmpl_dir):
    f = open(tmpl_dir +'/index.html', 'r')
    return f.read()    

def writeImg(dest_path, url):    
    fout = open(dest_path + '/' + getIMGFileName(url), 'w')
    fout.write(readFullPage(url))
    fout.close()
    
    
def getIMGFileName(url):
    rob = url
    while 1:
        ind = rob.find('/')
        if ind < 0:
            break 
        rob = rob[ind+1:]
    return rob    
######################################
### MAIN 
######################################
if len(argv) < 4:
    print "USAGE: ./export_help.py wiki_path tmplate_dir_path webbapp_help_path"
    print "wiki_path - provide base path to rike wiki pages like : 'https://github.com/arago/rike/wiki/'"
    print "tmplate_dir_path - provide path in workspace in which folder template of index.html is located"
    print "webbapp_help_path - provide path in workspace where to put resources"
    exit(1)
    
wiki_url = argv[1]
tmpl_dir = argv[2]
dest_path = argv[3] 

#(type, filename, content) if type img then content empty
resources = []

get_resources(wiki_url, resources)  

save_resources(dest_path, tmpl_dir, resources)
