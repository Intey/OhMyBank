(ns ombs.route
  (:require [compojure.core :refer [ANY POST GET defroutes wrap-routes]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.route :refer [not-found]]
            [ombs.core :as core]
            [ombs.handler :as handler]
            [noir.session :refer [wrap-noir-session]]
            ))

(defroutes main-routes
           (GET "/" [_] handler/index)
           (POST "/register" {params :params} (handler/register params))
           (GET  "/register" [_] handler/regpage)
           (not-found "Page not found") )

(def engine 
  (-> main-routes 
    (wrap-routes wrap-params)
    (wrap-routes wrap-noir-session)
    ))
