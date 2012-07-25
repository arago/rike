#!/bin/sh
currentdate=$(date +%Y%m%d%H%M%S)
find . -type d -name .svn -exec rm -rf '{}' ';'

checkinstall-arago -y -R "--spec=rpm.spec" "--arch=noarch" "--pkgsource=arago-repository" "--pkgrelease=$currentdate" "--nodoc" "--pakdir=." ./install.sh
