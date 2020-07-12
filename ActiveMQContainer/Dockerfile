FROM  	openjdk:8-jre-alpine
RUN 	wget -O /tmp/activemq.tar.gz http://archive.apache.org/dist/activemq/5.15.11/apache-activemq-5.15.11-bin.tar.gz
RUN 	tar -xzf /tmp/activemq.tar.gz -C /opt/
RUN 	rm -f /tmp/activemq.tar.gz
CMD 	["/opt/apache-activemq-5.15.11/bin/activemq", "console"]
EXPOSE 	8161
EXPOSE 	61616