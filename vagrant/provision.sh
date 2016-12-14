#!/usr/bin/env bash

echo "Setting up the hosts file"
echo "192.168.33.77 kafka-node" >> /etc/hosts

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

nohup /usr/bin/kafka-server-start /etc/kafka/server.properties &
nohup /bin/schema-registry-start /etc/schema-registry/schema-registry.properties &

cat >> /etc/kafka/connect-standalone.properties << EOF
key.converter.schema.registry.url=http://localhost:8081
value.converter.schema.registry.url=http://localhost:8081
offset.storage.file.filename=/tmp/connect.offsets
EOF

nohup /usr/bin/connect-standalone /etc/kafka/connect-standalone.properties /etc/kafka/connect-file-source.properties &

cat >> /etc/confluent-control-center/control-center.properties << EOF
confluent.controlcenter.internal.topics.partitions=1
confluent.controlcenter.internal.topics.replication=1
confluent.controlcenter.command.topic.replication=1
confluent.monitoring.interceptor.topic.partitions=1
confluent.monitoring.interceptor.topic.replication=1
EOF

nohup /usr/bin/control-center-start /etc/confluent-control-center/control-center.properties &

echo "insralling MariaDB"
echo "------------------"

cat >> /etc/yum.repos.d/MariaDB.repo << EOF
[mariadb]
name = MariaDB
baseurl = http://yum.mariadb.org/10.1/centos7-amd64
gpgkey=https://yum.mariadb.org/RPM-GPG-KEY-MariaDB
gpgcheck=1
EOF

yum -y install MariaDB-server MariaDB-client
sudo systemctl start mariadb

echo "finished provision.sh"
echo "---------------------"
