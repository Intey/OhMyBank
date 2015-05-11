(ns ombs.route
  (:require [compojure.core :refer [ANY POST GET defroutes wrap-routes]]
            [ring.middleware.params :refer [wrap-params]]
            ;[ring.middleware.reload :refer [wrap-reload]] ; don't work
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [compojure.route :refer [resources not-found]]
            [ombs.core :as core]
            [ombs.handler :as handler]
            [noir.session :refer [wrap-noir-session]]
            [noir.response :refer [redirect]]
            [noir.validation :refer [wrap-noir-validation]]
            ))

(defroutes main-routes
  (GET  "/" [params] handler/index)
  (GET  "/user" [_] handler/user)
  (POST "/login" request handler/login)
  (POST "/logout" request handler/logout)
  (POST "/register" {params :params} (handler/register params))
  (GET  "/register" [_] handler/regpage)
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
