#!/bin/sh
scp ./target/*.war wisdome3.devel.arago.de:/opt/liferay-portal-current/deploy/
./makerpm.sh

NAME=`fgrep Provides: rpm.spec |perl -pe 's/^\w+\:\ +//'|perl -pe 's/\r?\n//'`

ssh root@vm-repository.devel.arago.de mkdir -p /var/www/html/noarch/$NAME/
scp ./*.rpm root@vm-repository.devel.arago.de:/var/www/html/noarch/$NAME/
ssh root@vm-repository.devel.arago.de touch /tmp/repository-changed.flag
