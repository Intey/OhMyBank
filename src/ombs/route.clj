(ns ombs.route
  (:require 
    ;common routing. wraper for ...
    [compojure.core :refer [ANY POST GET defroutes wrap-routes]]
    ; 404, and resources for using css, js, html files.
    [compojure.route :refer [resources not-found]]
    ; decoupling wrappers
    [ring.middleware.params :refer [wrap-params]]
    ; colored stacktrace
    [ring.middleware.stacktrace :refer [wrap-stacktrace]]
    ; sic!
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]

    ;[ring.middleware.reload :refer [wrap-reload]] ; don't work

    [noir.session :refer [wrap-noir-session]]
    [noir.validation :refer [wrap-noir-validation]]
    [noir.response :refer [redirect]]

    ; request handlers. Prepare data, and call views. 
    [ombs.handler.handle :refer [index user]]
    [ombs.handler.addevent :refer [addevent-page addevent]]
    [ombs.handler.auth :refer [login logout register reg-page]]
    ))

(defroutes main-routes
  (resources "/") ; search all resources in dir 'resources' in root of project
  (GET  "/" [params] index)


  (POST "/login" request login)
  (POST "/logout" request logout)

  (POST "/register" {params :params} (register params))
  (GET  "/register" [_] reg-page)


  (GET "/addevent" [_] addevent-page)
  (POST "/addevent" {params :params} (addevent params))

  (GET  "/user" [_] user)

  ;payment controlling
  (POST "/pay" {params :params} (pay params))
  ;(POST "/confirm {params :params} (confirm-payment)

  ;(POST "/participate" [_] user) ;request common/participate) ; TODO: not fixed, after realize participation on addition

  (not-found "Page not found") ) ; should be last, it overlap all below routes.

(def engine
  (-> main-routes
    (wrap-routes wrap-params)
    ;(wrap-routes wrap-reload {:dirs "assets"})
    (wrap-routes wrap-stacktrace {:color? true})
    (wrap-routes wrap-keyword-params)
    (wrap-routes wrap-noir-session { :timeout (* 60 30) :timeout-response (redirect "/") })
    (wrap-routes wrap-noir-validation)
    ; wrap println - print any GETed and POSTed req|res to console.
    ))
