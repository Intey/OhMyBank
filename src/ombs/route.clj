(ns ombs.route
  (:require [compojure.core :refer [ANY POST GET defroutes wrap-routes]]
            [compojure.route :refer [resources not-found]]
            [ombs.handler :as handler]
            [ombs.auth :as auth]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [noir.session :refer [wrap-noir-session]]
            [noir.response :refer [redirect]]
            [noir.validation :refer [wrap-noir-validation]]
            [ring.adapter.jetty9 :refer [send!]]
            ))

(defroutes main-routes
  (GET  "/" [params] handler/index)
  (GET  "/user" [_] handler/user)
  (POST "/login" request auth/login)
  (POST "/logout" request auth/logout)
  (POST "/register" {params :params} (auth/register params))
  (GET  "/register" [_] auth/regpage)
  (POST "/addevent" request handler/add-event)
  (POST "/participate" request handler/participate)
  (GET "/ws" request handler/ws-h)
  (resources "/")
  (not-found "Page not found") )

(def engine
  (-> 
    main-routes
    (wrap-routes wrap-params)
    ;(wrap-routes wrap-reload {:dirs "assets"})
    (wrap-routes wrap-stacktrace {:color? true})
    (wrap-routes wrap-keyword-params)
    (wrap-routes wrap-noir-session { :timeout (* 60 30) :timeout-response (redirect "/") })
    (wrap-routes wrap-noir-validation)
    )
)

