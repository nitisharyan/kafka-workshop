#!/usr/bin/env bash

echo "Setting up the hosts file"
echo "192.168.33.17 kafka-node" >> /etc/hosts

echo "installing zookeeper"
echo "--------------------"

sudo yum -y install java-1.8.0-openjdk
rpm -Uvh http://archive.cloudera.com/cdh4/one-click-install/redhat/6/x86_64/cloudera-cdh-4-0.x86_64.rpm
sudo yum -y install zookeeper zookeeper-server

sudo -u zookeeper zookeeper-server-initialize --myid=1
sudo service zookeeper-server start

echo "installing the confluent platform"
echo "---------------------------------"

sudo rpm --import http://packages.confluent.io/rpm/3.1/archive.key

cat >> /etc/yum.repos.d/confluent.repo << EOF
[Confluent.dist]
name=Confluent repository (dist)
baseurl=http://packages.confluent.io/rpm/3.1/7
gpgcheck=1
gpgkey=http://packages.confluent.io/rpm/3.1/archive.key
enabled=1

[Confluent]
name=Confluent repository
baseurl=http://packages.confluent.io/rpm/3.1
gpgcheck=1
gpgkey=http://packages.confluent.io/rpm/3.1/archive.key
enabled=1
EOF

sudo yum clean all
sudo yum -y install confluent-platform-oss-2.11
sudo yum -y install confluent-control-center


echo "finished provision.sh"
echo "---------------------"
