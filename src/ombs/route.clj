(ns ombs.route
  (:require [compojure.core :refer [ANY POST GET defroutes wrap-routes]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [compojure.route :refer [not-found]]
            [ombs.core :as core]
            [ombs.handler :as handler]
            [noir.session :refer [wrap-noir-session]]
            [noir.response :refer [redirect]]
            ))

(defroutes main-routes
  (GET  "/" [params] handler/index)
  (GET  "/user" [_] handler/user)
  (POST "/login" request handler/login)
  (POST "/logout" request handler/logout)
  (POST "/register" {params :params} (handler/register params))
  (GET  "/register" [_] handler/regpage)
  (POST "/addevent" request handler/add-event)
  (not-found "Page not found") )

(def engine 
  (-> main-routes 
    (wrap-routes wrap-params)
    (wrap-routes wrap-keyword-params)
    (wrap-routes wrap-noir-session { :timeout (* 60 30) :timeout-response (redirect "/") })
    ))
