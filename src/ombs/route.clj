(ns ombs.route
  (:require
    ;common routing. wraper for ...
    [compojure.core :refer [ANY POST GET defroutes wrap-routes]]
    ; 404, and resources for using css, js, html files.
    [compojure.route :refer [resources not-found]]
    ; decoupling wrappers
    [ring.middleware.params :refer [wrap-params]]
    ; colored stacktrace
    [ring.middleware.stacktrace :refer [wrap-stacktrace]]
    ; sic!
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]

    ;[ring.middleware.reload :refer [wrap-reload]] ; don't work
    [cheshire.core :as json]

    [noir.session :refer [wrap-noir-session]]
    [noir.validation :refer [wrap-noir-validation]]
    [noir.response :refer [redirect]]
    ;[ombs.db.old :refer [get-events]]

    ; request handlers. Prepare data, and call views.
    [ombs.handler.pages :as pages]
    [ombs.handler.eventacts :refer [pay participate start]]
    [ombs.handler.adminacts :refer [affirm refute moneyout]]
    [ombs.handler.addevent :refer [init-event]]
    [ombs.handler.auth :refer [login logout register reg-page]]
    [ombs.handler.api :as api]
    [ombs.funcs :refer [parse-int]]
    ))

(defroutes main-routes
  (GET  "/" [_] (pages/index))

  (POST "/login" request login)
  (POST "/logout" request logout)

  (POST "/register" {params :params} (register params))
  (GET  "/register" [_] (reg-page))


  (GET "/addevent" [_] (pages/addevent))
  (POST "/addevent" {params :params} (init-event params))

  (GET "/moneyout" [_] (pages/moneyout))
  (POST "/moneyout" {{money :money username :target} :params} (moneyout money username))

  (GET  "/user" [_] (pages/user))

  (GET "/start" {params :params} (start params))
  (GET "/participate" {params :params} (participate params))
  (GET "/pay" {params :params} (pay params))

  ; Like REST API
  (GET "/affirm" {{fid :fid} :params} (affirm fid))
  (GET "/refute" {{fid :fid} :params} (refute fid))

  ; Just test tool
  (ANY "/api/pong" {params :params} (json/generate-string params))

  (GET "/api/help" [_] (api/help))
  (GET "/api/events" {{types :types} :params} (api/get-events types))


  (resources "/") ;Should be after pages. Search all css, js, etc. in dir 'resources' in root of project
  (not-found "Page not found")
  ) ; should be last, it overlap all below routes.

(def engine
  (-> main-routes
    (wrap-routes wrap-params)
    ;(wrap-routes wrap-reload {:dirs "assets"})
    (wrap-routes wrap-stacktrace {:color? true})
    (wrap-routes wrap-keyword-params)
    (wrap-routes wrap-noir-session { :timeout (* 60 30) :timeout-response (redirect "/") })
    (wrap-routes wrap-noir-validation)
    ; wrap println - print any GETed and POSTed req|res to console.
    ))
