(ns ombs.route
  (:require [compojure.core :refer [ANY POST GET defroutes wrap-routes]]
            [ombs.core :as core]
            [ombs.handler :as handler]
            [ombs.auth :as auth]
            [ring.middleware.params :refer [wrap-params]]
            ;[ring.middleware.reload :refer [wrap-reload]] ; don't work
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [compojure.route :refer [resources not-found]]
            [noir.session :refer [wrap-noir-session]]
            [noir.response :refer [redirect]]
            [noir.validation :refer [wrap-noir-validation]]
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
  (resources "/")
  (not-found "Page not found") )

(def engine
  (-> main-routes
    (wrap-routes wrap-params)
    ;(wrap-routes wrap-reload {:dirs "assets"})
    (wrap-routes wrap-stacktrace {:color? true})
    (wrap-routes wrap-keyword-params)
    (wrap-routes wrap-noir-session { :timeout (* 60 30) :timeout-response (redirect "/") })
    (wrap-routes wrap-noir-validation)
    ))
