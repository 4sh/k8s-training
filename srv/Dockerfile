FROM garthk/unzip as build

WORKDIR /distrib

COPY build/distributions/conduit.zip /distrib/

RUN unzip conduit.zip

FROM openjdk:11-jdk as production

COPY --from=build /distrib/conduit /usr/local/conduit/

RUN chmod +x /usr/local/conduit/bin/conduit

ENTRYPOINT /usr/local/conduit/bin/conduit
