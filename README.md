# K8s training

## Env

replace xxx with your trigram in lowercase, eg xhn 
`export ME=xxx`

## Publish your docker images

in `ui`

build:
```
docker build -t europe-docker.pkg.dev/quatreapp/k8straining/myapp-ui-$ME .
```

in `srv`

build:
```
./gradlew build
docker build -t europe-docker.pkg.dev/quatreapp/k8straining/myapp-srv-$ME .
```

push:
```
docker push europe-docker.pkg.dev/quatreapp/k8straining/myapp-ui-$ME
docker push europe-docker.pkg.dev/quatreapp/k8straining/myapp-srv-$ME
```

## apply the kubernetes config

```
cat k8s/*.yaml | sed s/@ME/$ME/g | kubectl apply -f -
```

## check after a minute:

```
open https://4sh-formation-$ME.quatre.app
```
