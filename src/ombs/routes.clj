(ns ombs.routes 
  (:require [compojure.core :refer [POST GET defroutes]]  
            [compojure.handler :refer [site]]             
            [compojure.route :refer [not-found]]
            [ombs.core :as core]
            ))

(defroutes main-routes
           (GET "/" [] (core/index))
           (POST "/some_action" [value] (core/summary value))
           (not-found "Page not found"))

(def engine
  (site main-routes))
