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
    [ombs.handler.pages :as pages]
    [ombs.handler.eventacts :refer [pay pay-part participate start]]
    [ombs.handler.addevent :refer [init-event]]
    [ombs.handler.auth :refer [login logout register reg-page]]
    [ombs.funcs :refer [parse-int]]
    ))

(defroutes main-routes
  (GET  "/" [_] (pages/index))

  (POST "/login" request login)
  (POST "/logout" request logout)

  (POST "/register" {params :params} (register params))
  (GET  "/register" [_] (reg-page))


  (GET "/addevent" [_] (pages/addevent))
  (POST "/addevent" {params :params} (init-event params))

  (GET  "/user" [_] (pages/user))

  (POST "/act" {params :params}
        (case (:action params)
          "pay" (if-let [parts (parse-int (:parts params))]
                  (pay-part params)
                  (pay params))
          "participate" (participate params)
          "start" (start params)))

  ;payment controlling
  ;(POST "/confirm {params :params} (confirm-payment)

  ;(POST "/pay" {params :params} (println (str "paying! Params:" params)))
  ;(POST "/participate" {params :params} (println (str "participating! Params:" params))) ;request common/participate) ; TODO: not fixed, after realize participation on addition

  (resources "/") ;Should be after pages. Search all css, js, etc. in dir 'resources' in root of project

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
