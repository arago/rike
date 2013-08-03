![rike-logo](https://github.com/arago/rike/raw/master/logo.png)

EC2 AMI Quickstart:
=============

* launch a [rike AMI 4.1](https://aws.amazon.com/amis/rike-ami-4-1) instance
  * medium or larger size required
  * the network security group requires the port 443 to be open, 22 (for remote administration via ssh) and 80 (which redirects to 443) are optional
* wait 5 to 10 minutes until the machine installation completes
* navigate to https://[your amazon instance.amazonaws.com]/
* log in with user=rike-admin@arago.de and password=rikeadmin123$
* change the admin password (and email)
