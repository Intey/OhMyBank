(ns ombs.route
  (:require [compojure.core :refer [ANY POST GET defroutes wrap-routes]]
            [ombs.core :as core]
            [ombs.handler.common :refer [index user]]
            [ombs.handler.addevent :refer [addevent-page addevent]]
            [ombs.handler.auth :refer [login logout register reg-page]]
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
  (GET  "/" [params] index)
  (GET  "/user" [_] user)
  (POST "/login" request login)
  (POST "/logout" request logout)
  (POST "/register" {params :params} (register params))
  (GET  "/register" [_] reg-page)
  (GET "/addevent" [_] addevent-page)
  (POST "/participate" [_] user);request common/participate) ; TODO: not fixed, after realize participation on addition
  (POST "/addevent" {params :params} (addevent params))
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
    ; wrap println
    ))
