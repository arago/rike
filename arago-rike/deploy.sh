#!/bin/sh
rm -rf target
mvn package  $@
cp target/*.war /opt/liferay-portal-current/deploy/
