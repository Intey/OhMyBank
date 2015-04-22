(ns ombs.route
  (:require [compojure.core :refer [ANY POST GET defroutes]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [not-found]]
            [ombs.core :as core]
            [ombs.handler :as handler]
            [cheshire.core :refer [generate-string]]            
            [liberator.core :refer [defresource resource request-method-in]]
            ))

; use atom, to hold the list of users
(def users (atom ["John" "Jane"]))
;return content of users atom as JSON


(defresource get-users
             :allowed-methods [:get]
             :handle-ok (fn [_] (generate-string @users))
             :available-media-types ["application/json"]
             )
;add user
(defresource add-user 
             :method-allowed? (request-method-in :post)
             :post! (fn [context]
                      (let [params (get-in context [:request :form-params])]
                        (swap! users conj (get params "user")) ))
             :handle-created (fn [_] (generate-string @users))
             :available-media-types ["application/json"]
             )

(defroutes main-routes
           (GET "/" request handler/index)
           (ANY "/add-user" request add-user)
           (ANY "/users" request get-users)
           (not-found "Page not found"))

(def engine
  (site main-routes))
