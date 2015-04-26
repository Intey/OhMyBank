(ns ombs.route
  (:require [compojure.core :refer [ANY POST GET defroutes]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [not-found]]
            [ombs.core :as core]
            [ombs.handler :as handler]
            [noir.io :as io]
            [clojure.java.io :refer [file]]
            [cheshire.core :refer [generate-string]]            
            [liberator.core :refer [defresource resource request-method-in]]
            ))

; use atom, to hold the list of users
(def users (atom ["John" "Jane"]))
;return content of users atom as JSON

(defresource index 
             :available-media-types ["text/html"]
             :exists?  (fn [context]
                         [ (io/get-resource "/index.html") 
                           {::file (file (str (io/resource-path) "/index.html"))} ])
             :handle-ok (fn [ {{{resource  :resource} :route-params} :request}]
                          (clojure.java.io/input-strea (io/get-resource "/index.html")))
             :last-modified (fn [{{{resource :resource} :route-params} :request}]
                              (.lastModified (file (str (io/resource-path) "index.html"))))
             )

(defresource get-users
             :allowed-methods [:get]
             :handle-ok (fn [_] (generate-string @users))
             :available-media-types ["application/json"]
             )
;add user
(defresource add-user 
             :method-allowed? (request-method-in :post)
             :malformed? (fn [context]
                            (let [params (get-in context [:request :form-params])]
                              (empty? (get params "user"))))
             :handle-malformed "user name cannot be empty"
             :post! (fn [context]
                      (let [params (get-in context [:request :form-params])]
                        (swap! users conj (get params "user")) ))
             ;this evals before decision graph is run, so if not wrap (generate-string ...) with fn
             ;for first time we get old user list
             :handle-created (fn [_] (generate-string @users))
             :available-media-types ["application/json"]
             )

(defroutes main-routes
           (GET "/" request handler/index)
           (ANY "/add-user" request add-user)
           (ANY "/users" request get-users)
           (ANY "/" request index)
           (not-found "Page not found"))

(def engine
  (site main-routes))
