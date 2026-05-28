# Итоговая работа по DevOps

**Студент:** Котлов Никита  
**Группа:** 11-306  
**Проект:** MoviesByMood

## Задание

Необходимо запаковать веб-приложение в контейнеры с использованием Docker Compose, настроить централизованный сбор логов приложения и обеспечить просмотр логов через Grafana.

Также в работе реализовано дополнительное задание: развёртывание приложения в Minikube через Kubernetes-манифесты.

## Краткое описание проекта

MoviesByMood — веб-приложение на Spring Boot для подбора фильмов по настроению.

В приложении реализованы авторизация, каталог фильмов, фильтрация по параметрам и работа с PostgreSQL. В рамках итоговой DevOps-работы приложение контейнеризовано и запускается вместе с базой данных, Loki, Promtail и Grafana.

## Используемые технологии

- Java 20
- Spring Boot
- Maven
- PostgreSQL
- Docker
- Docker Compose
- Loki
- Promtail
- Grafana
- Minikube
- Kubernetes

## Состав Docker Compose

| Сервис | Назначение |
|---|---|
| `app` | веб-приложение MoviesByMood |
| `postgres` | база данных PostgreSQL |
| `loki` | централизованное хранилище логов |
| `promtail` | сборщик логов приложения |
| `grafana` | просмотр логов через веб-интерфейс |

## Сборка образа

Сборка приложения выполняется через bash-скрипт `build.sh`.

Скрипт принимает параметр `-t`, который задаёт тег собираемого Docker-образа.

```bash
./build.sh -t v1
```

В `Dockerfile` используется multistage-сборка:

1. на первом этапе Maven собирает Spring Boot приложение;
2. на втором этапе готовый `.jar` файл запускается в отдельном runtime-образе.

## Запуск проекта

Запуск контейнеров выполняется через bash-скрипт `run.sh`.

Скрипт принимает параметр `-t`, который задаёт тег запускаемого образа.

```bash
./run.sh -t v1
```

После запуска приложение доступно по адресу:

```text
http://localhost:8081
```

Grafana доступна по адресу:

```text
http://localhost:3000
```

Данные для входа в Grafana:

```text
admin / admin
```

## Проверка контейнеров

Проверить запущенные контейнеры можно командой:

```bash
docker compose ps
```

Ожидаемые контейнеры:

- `moviesbymood-app`
- `moviesbymood-postgres`
- `moviesbymood-loki`
- `moviesbymood-promtail`
- `moviesbymood-grafana`

## Проверка логирования

Приложение пишет логи в файлы в директории `logs/`.

Проверка файлов логов:

```bash
ls -la logs
tail -n 40 logs/core.log
```

Promtail читает log-файлы приложения и отправляет их в Loki.

Проверка Promtail:

```bash
docker compose logs promtail --tail=80
```

Проверка готовности Loki:

```bash
curl http://localhost:3100/ready
```

Ожидаемый результат:

```text
ready
```

## Просмотр логов в Grafana

Для просмотра логов нужно открыть Grafana:

```text
http://localhost:3000
```

Далее перейти:

```text
Explore -> Loki
```

Запрос для просмотра логов приложения:

```logql
{app="moviesbymood"}
```

В работе собираются именно логи приложения, а не метрики.

## Скриншоты основной части

| № | Скриншот | Что подтверждает |
|---|---|---|
| 1 | [01_build_success.png](screenshots/01_build_success.png) | успешная сборка Docker-образа через `build.sh -t v1` |
| 2 | [02_run_success.png](screenshots/02_run_success.png) | запуск проекта через `run.sh -t v1` |
| 3 | [03_compose_containers.png](screenshots/03_compose_containers.png) | все контейнеры Docker Compose запущены |
| 4 | [04_app_login.png](screenshots/04_app_login.png) | приложение открылось в браузере |
| 5 | [05_app_movies_page.png](screenshots/05_app_movies_page.png) | рабочая страница каталога фильмов |
| 6 | [06_file_logs.png](screenshots/06_file_logs.png) | приложение пишет логи в файлы |
| 7 | [07_promtail_reads_logs.png](screenshots/07_promtail_reads_logs.png) | Promtail читает log-файлы приложения |
| 8 | [08_loki_ready.png](screenshots/08_loki_ready.png) | Loki запущен и готов принимать логи |
| 9 | [09_grafana_loki_query.png](screenshots/09_grafana_loki_query.png) | в Grafana выбран источник данных Loki |
| 10 | [10_grafana_logs_result.png](screenshots/10_grafana_logs_result.png) | Grafana показывает объём логов |
| 11 | [11_grafana_logs_lines.png](screenshots/11_grafana_logs_lines.png) | Grafana показывает строки логов приложения |

## Дополнительное задание: Minikube

Дополнительно проект развёрнут в Minikube через Kubernetes-манифесты.

Namespace создан по маске `<имя><дата>`:

```text
kotlov20260516
```

Манифесты находятся в папке:

```text
k8s/
```

## Состав Kubernetes-манифестов

| Файл | Назначение |
|---|---|
| `k8s/namespace.yml` | создание namespace `kotlov20260516` |
| `k8s/postgres.yml` | PostgreSQL, Secret, PVC и Service |
| `k8s/app.yml` | приложение MoviesByMood и Service |
| `k8s/loki.yml` | Loki и Service |
| `k8s/grafana.yml` | Grafana и Service |

## Запуск в Minikube

Запуск Minikube:

```bash
minikube start --driver=docker --memory=4096 --cpus=2
```

Проверка node:

```bash
kubectl get nodes
```

Загрузка образа приложения в Minikube:

```bash
minikube image load moviesbymood-app:v1
```

Проверка загруженного образа:

```bash
minikube image ls | grep moviesbymood
```

Применение манифестов:

```bash
kubectl apply -f k8s/namespace.yml
kubectl apply -f k8s/postgres.yml
kubectl apply -f k8s/loki.yml
kubectl apply -f k8s/grafana.yml
kubectl apply -f k8s/app.yml
```

Проверка namespace:

```bash
kubectl get namespaces
```

Проверка сервисов:

```bash
kubectl get services -n kotlov20260516
```

Проверка pod-ов:

```bash
kubectl get pods -n kotlov20260516
```

Проверка всех объектов:

```bash
kubectl get all -n kotlov20260516
```

Проброс порта приложения:

```bash
kubectl port-forward -n kotlov20260516 svc/moviesbymood-app 8081:8081
```

После этого приложение доступно по адресу:

```text
http://localhost:8081
```

## Скриншоты Minikube

| № | Скриншот | Что подтверждает |
|---|---|---|
| 12 | [12_minikube_tools_check.png](screenshots/12_minikube_tools_check.png) | установлены Minikube и kubectl |
| 13 | [13_minikube_node_ready.png](screenshots/13_minikube_node_ready.png) | Minikube запущен, node находится в статусе `Ready` |
| 14 | [14_minikube_image_loaded.png](screenshots/14_minikube_image_loaded.png) | Docker-образ приложения загружен в Minikube |
| 15 | [15_k8s_namespaces.png](screenshots/15_k8s_namespaces.png) | создан namespace `kotlov20260516` |
| 16 | [16_k8s_services.png](screenshots/16_k8s_services.png) | созданы Kubernetes services |
| 17 | [17_k8s_pods.png](screenshots/17_k8s_pods.png) | pod-ы запущены и находятся в статусе `Running` |
| 18 | [18_k8s_all.png](screenshots/18_k8s_all.png) | общий список Kubernetes-объектов |
| 19 | [19_k8s_port_forward_app.png](screenshots/19_k8s_port_forward_app.png) | выполнен port-forward приложения |
| 20 | [20_k8s_app_login.png](screenshots/20_k8s_app_login.png) | приложение открылось через Minikube |
| 21 | [21_k8s_movies_page.png](screenshots/21_k8s_movies_page.png) | страница каталога фильмов открылась через Minikube |

## Остановка проекта

Остановка Docker Compose:

```bash
docker compose down
```

Остановка Minikube:

```bash
minikube stop
```

## Итог

В результате выполнено:

- веб-приложение упаковано в контейнеры;
- зависимости приложения запускаются через Docker Compose;
- сборка образа реализована через multistage Dockerfile;
- сборка и запуск выполняются через bash-скрипты с параметром `-t`;
- настроен сбор логов приложения через Promtail;
- Loki используется как централизованное хранилище логов;
- Grafana используется для просмотра логов приложения;
- дополнительно выполнено развёртывание проекта в Minikube через Kubernetes-манифесты.