FROM tomcat:9.0.52-jdk11-adoptopenjdk-hotspot

RUN set -ex; \
    rm -rf ${CATALINA_HOME}/webapps/*; \
    rm -rf ${CATALINA_HOME}/server/webapps/*; \
    mkdir -p ${CATALINA_HOME}/lib/org/apache/catalina/util; \
    mkdir -p ${CATALINA_HOME}/conf/Catalina/localhost; \
    echo 'server.info=Apache Tomcat' > ${CATALINA_HOME}/lib/org/apache/catalina/util/ServerInfo.properties; \
    sed -i 's#<Connector port="8080"#<Connector port="8080" server="Apache"#' ${CATALINA_HOME}/conf/server.xml;

ENV CATALINA_OPTS="-DRDS_DB_NAME=postgres -DRDS_USERNAME=postgres -DRDS_PASSWORD=changeme -DRDS_HOSTNAME=example -DRDS_PORT=5432"

CMD ["catalina.sh", "run"]

COPY target/ROOT.war ${CATALINA_HOME}/webapps/ROOT.war
