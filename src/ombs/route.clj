(ns ombs.route
  (:require [compojure.core :refer [POST GET defroutes]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [not-found]]
            [ombs.core :as core]
            [ombs.handler :as handler]
            [noir.response :refer [redirect]]))

(defroutes main-routes
           (GET "/" [] (handler/index) )
           (POST "/login" [params] (handler/user params))
           ;(if (= (:registry params) "true")
           ;  (if (= (:pass params) (:pass1 params))
           ;    ;(redirect "/") ;if ok
           ;    ;(handler/registration-page) ; if fail
           ;    (handler/index params)
           ;    )
           ;  )

           ;(if (core/check-acc (:username params) (:pass params))
           ;  ;(redirect "/") ; mksession
           ;  (handler/index params)
           ;  )
           (GET "/user" [params]
                (handler/user params))

           (GET "/user/:username" {{params :username} :params}
                (handler/user params)
                )
           (not-found "Page not found"))

(def engine
  (site main-routes))
