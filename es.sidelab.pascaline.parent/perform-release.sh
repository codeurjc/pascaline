#!/bin/sh

if [ $# -ne 4 ]
then
  echo "Usage: `basename $0` {tagVersion} {nextVersion} {username} {password}"
  echo "Run this script from the parent project"
  exit
fi

timestamp=`date -u +%Y%m%d%H%M`
tagVersion=$1
# With this approach, p2 generation fails saying that version-timestamp is not a
# valid sustitution pattern for version.qualifier
#releaseVersion=$tagVersion-$timestamp
releaseVersion=$tagVersion.$timestamp
nextVersion=$2.qualifier

echo "tagVersion=$tagVersion"
echo "releaseVersion=$releaseVersion"
echo "nextVersion=$nextVersion"
echo "username=$3"
#echo -e "Are you sure (y/N)? \c "
#read confirm
#if [ "-"$confirm != "-y" ]; then
#  exit
#fi

export M2_HOME=/home/usuario/maven3
export M2=$M2_HOME/bin
export MAVEN_OPTS=-Dorg.apache.maven.global-settings=/home/usuario/maven3/conf/settings.xml
export PATH=$PATH:$M2

cd trunk/es.sidelab.pascaline.parent

echo "Preparing release versions"
mvn org.sonatype.tycho:tycho-versions-plugin:set-version -Dtycho.mode=maven -DnewVersion=$releaseVersion

echo "Performing build with the release version"
mvn clean deploy

echo "Committing changes (preparing for tagging)"
cd ..
svn ci -m "prepare release pascaline-ide-$tagVersion" --username $3 --password $4

echo "Tagging release"
svn copy https://code.sidelab.es/svn/pascaline/ide/trunk/ https://code.sidelab.es/svn/pascaline/ide/tags/pascaline-ide-$tagVersion -m "copy for tag pascaline-ide-$tagVersion" --username $3 --password $4

echo "Preparing next development version"
cd es.sidelab.pascaline.parent
mvn org.sonatype.tycho:tycho-versions-plugin:set-version -Dtycho.mode=maven -DnewVersion=$nextVersion

echo "Committing changes to trunk (trunk now contains the new development version)"
cd ..
svn ci -m "prepare for next development iteration" --username $3 --password $4

echo "Done!"
