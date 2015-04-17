(ns ombs.routes 
  (:require [compojure.core :refer [POST GET defroutes]]  
            [compojure.handler :refer [site]]             
            [compojure.route :refer [not-found]]
            [ombs.core :as core]
            [ring.util.response :refer [redirect]])) 

(defroutes main-routes
  (GET "/" [] (core/index))
  (POST "/login" [params]
        (if (= (:registry params) "true") 
          (if (= (:pass params) (:pass1 params))
            ;(redirect "/") ;if ok 
            ;(core/registration-page) ; if fail 
            (core/index params)
            (core/index params)
            
            )
          (if (core/check-acc (:username params) (:pass params))
            ;(redirect "/") ; mksession
            (core/index params)
            (core/index params)
            )))
  (not-found "Page not found"))

(def engine
  (site main-routes))
