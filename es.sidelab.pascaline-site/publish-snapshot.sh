#!/bin/bash

# Usage: 
# publish-snapshot.sh <eclipse-dir> <remote-repo> <maven-gen-repo> <tmp-repo> <user> <pass> <remote-dir>

if [ $# -ne 8 ]
then
  echo "Usage: `basename $0` {eclipse-dir} {remote-repo} {maven-gen-repo} {tmp-repo} {user} {pass} {host} {remote-dir}"
  echo "Run this script from the parent project"
  exit
fi

eclipsedir=$1
remotep2=$2
mavenp2=$3
tmpp2=$4
username=$5
password=$6
host=$7
remotedir=$8

echo "eclipsedir=$eclipsedir"
echo "remotep2=$remotep2"
echo "mavenp2=$mavenp2"
echo "tmpp2=$tmpp2"
echo "username=$username"
# It is not a good idea, because it appears in Jenkins logs.
#echo "password=$password"
echo "host=$host"
echo "remotedir=$remotedir"

echo "Cleaning tmp repo: $tmpp2"

rm -Rf $tmpp2
mkdir $tmpp2

echo "Mirroring artifacts..."

# Artifact mirroring
$eclipsedir/eclipse -nosplash -consolelog -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source $remotep2 -destination file:$tmpp2
$eclipsedir/eclipse -nosplash -consolelog -application org.eclipse.equinox.p2.artifact.repository.mirrorApplication -source $mavenp2 -destination file:$tmpp2

echo "Mirroring metadata..."

# Metadata mirroring
$eclipsedir/eclipse -nosplash -consolelog -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source $remotep2 -destination file:$tmpp2
$eclipsedir/eclipse -nosplash -consolelog -application org.eclipse.equinox.p2.metadata.repository.mirrorApplication -source $mavenp2 -destination file:$tmpp2

echo "Uploading repo..."

lftp -u ${username},${password} sftp://${host} <<EOF
mrm $remotedir/*
mrm $remotedir/features/*
mrm $remotedir/plugins/*
mirror -R $tmpp2 $remotedir
bye
EOF
