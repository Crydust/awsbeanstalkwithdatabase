FROM tomcat:9.0.8-jre10-slim

RUN set -ex; \
    rm -rf ${CATALINA_HOME}/webapps/*; \
    rm -rf ${CATALINA_HOME}/server/webapps/*; \
    mkdir -p ${CATALINA_HOME}/lib/org/apache/catalina/util; \
    mkdir -p ${CATALINA_HOME}/conf/Catalina/localhost; \
    echo 'export CATALINA_OPTS="-DRDS_DB_NAME=postgres -DRDS_USERNAME=postgres -DRDS_PASSWORD=dtmqEqrKc67ykGc -DRDS_HOSTNAME=example_db -DRDS_PORT=5432"' > ${CATALINA_HOME}/bin/setenv.sh; \
    chmod a+x ${CATALINA_HOME}/bin/setenv.sh; \
    echo 'server.info=Apache Tomcat' > ${CATALINA_HOME}/lib/org/apache/catalina/util/ServerInfo.properties; \
    sed -i 's#<Connector port="8080"#<Connector port="8080" server="Apache"#' ${CATALINA_HOME}/conf/server.xml;

#CMD ["catalina.sh", "run", "-security"]
CMD ["catalina.sh", "run"]

#COPY docker-context.xml ${CATALINA_HOME}/conf/Catalina/localhost/ROOT.xml
COPY target/ROOT.war ${CATALINA_HOME}/webapps/ROOT.war
