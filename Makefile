APP_IMAGE_LIST ?= user-service category-service product-service auth-service gateaway-service order-service email-service db user-db redis

start : down remove up

down :
	docker compose down

remove :
	docker image rm -f ${APP_IMAGE_LIST}

up:
	docker compose up -d

build:
	docker compose build

restart: down up

env:
	cp .env.example .env
	nano .env