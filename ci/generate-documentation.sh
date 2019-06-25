#!/bin/sh

set -e

# build pitest fix report aggregation fix
git clone --branch fix/report-aggregation https://github.com/aurelien-baudet/pitest.git $HOME/pitest
mvn clean install -DskipTests -f $HOME/pitest -q -B > $HOME/pitest-build.log

./mvnw clean install post-site -Pmutation-testing -Dmaven.test.redirectTestOutputToFile=true -Dsurefire.useSystemClassLoader=false -Dfailsafe.useSystemClassLoader=false -B
# FIXME: mutation testing produces surefire and failsafe reports => must build, test and generate site again...
./mvnw install post-site -Dmaven.test.redirectTestOutputToFile=true -Dsurefire.useSystemClassLoader=false -Dfailsafe.useSystemClassLoader=false -B

