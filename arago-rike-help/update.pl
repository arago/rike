#!/usr/bin/perl
use strict;
use warnings;
use Cwd; 

my $root 		= getcwd().'/src/main/webapp/help/';
my $wiki 		= qw(http://wiki.arago.de/);
my $attach 	= $wiki.qw(attach/);
my $base 		= $wiki.qw(asHTML.html?page=%s&encoding=UTF-8);
my $ext  		= '.html';

sub fetchPage
{
  my $page 		= shift;
  my $file    = $root.$page.$ext;
  
  return if -e $file;
  
  warn "fetching page $page";
  
  my $url 	  = sprintf($base, $page);
  my $content = qx(curl --silent $url);
  my @toFetch = ();
  
  for my $child ($content =~ /page=([^\"\&]+)/gis)
  {
    my $tmp = quotemeta("http://wiki.arago.de:80/Wiki.jsp?page=".$child);
    
    $content =~ s/$tmp/$child$ext/;
    
    push(@toFetch, $child);
  }

  for my $image ($content =~ /<img\ *src=\"([^\"]+attach[^\"]+?)\"/gis)
  {
    my $tmp = (split(/\//, $image))[-1];
    
    system("curl --silent -o '$root$page.$tmp' '$attach$page/$tmp' ") unless -e "$attach$page/$tmp";
    
    $image = quotemeta($image);
    
    $content =~ s/$image/$page.$tmp/g;
  }
  
  open(my $fh, ">", $file) or die $!;
  print $fh $content;
  close($fh);
  
	map(sub {fetchPage($_)}->($_), @toFetch);  
}

system("rm -rf $root*");

fetchPage('help.rike.index');
