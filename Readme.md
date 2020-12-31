# This is a project for aliyun DDns.

## Set up
1. java 11
2. maven
3. docker
4. raspberry
5. set up your aliyun config in config file, env or whatever spring support.
```yaml
aliyun:
  access:
    region-id: {REGION_ID}
    key: {KEY}
    secret: {SECRET}
  update:
    domain-name: {DOMAIN_NAME}
    keyword: {KEY_WORD}
    type: {TYPE}
```


## Build
```shell
mvn clean package
docker build -t {tag} .
```

## Run
```shell
docker-compose up -d
```