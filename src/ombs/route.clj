(ns ombs.route
  (:require
    ;common routing. wraper for ...
    [compojure.core :refer [ANY POST GET PUT DELETE defroutes wrap-routes context]]
    ; 404, and resources for using css, js, html files.
    [compojure.route :refer [resources not-found]]
    ; colored stacktrace
    [ring.middleware.stacktrace :refer [wrap-stacktrace]]

    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.keyword-params :refer [wrap-keyword-params]]
    [ring.middleware.nested-params :refer [wrap-nested-params]]
    [ring.middleware.json :refer [wrap-json-response
								  wrap-json-body
								  wrap-json-params]]
	[ring.util.request :as reqh]
	[ring.util.response :as resh]

    [cheshire.core :as json]

    [noir.session :refer [wrap-noir-session]]
    [noir.validation :refer [wrap-noir-validation]]
    [noir.response :refer [redirect]]

    ; request handlers. Prepare data, and call views.
    [ombs.handler.eventacts :refer [pay participate start]]
    [ombs.handler.adminacts :refer [affirm refute moneyout]]
    [ombs.handler.addevent :refer [init-event]]
    [ombs.handler.auth :refer [login logout register]]
    [ombs.handler.api :as api]
    [ombs.funcs :refer [parse-int]]
    [clojure.pprint :refer [pprint]]

    [clojure.java.io :as io]
	)
  (:import (java.io ByteArrayInputStream
					ByteArrayOutputStream))
  )

(defn wrap-content-json [handler]
  (fn [req]
      (-> (handler req)
          (assoc-in [:headers "Content-Type"] "application/json; charset=utf-8")
          ;(assoc-in [:content-type] "application/json")
          )))


(defn with-debug
  ([value] (pprint value) value)
  ([f & args] (pprint args) (apply f args)))

(defroutes api
  (context "/api" [req]
           (GET "/" [] (api/help))
           (GET "/test" [] (json/generate-string {:foo 123}))
           (context "/events" []
                    (GET "/" [] (api/get-events))
                    (POST "/" [] "")
                    (context "/:id" [id]
							 (defroutes event
							   (GET "/" [] id)
							   (PUT "/" {body :body} (str "updating event: " (with-debug body)))
							   (DELETE "/" [] id)

							   (context "/participants" []
										(GET "/" {params :params} (str "no participants with params " (with-debug params)))
										(POST "/" {params :params} (str "add new participants: " (with-debug params)))
										)
							   (POST "/start" [_] "starting event: " id)
							   )
							 )
                    )

           (context "/users" []
                    (defroutes user
                      (GET "/" [] (str []))
                      (POST "/" {body :body} body)
                      (context "/:id" [id]
                               (GET "/" [] id)
                               (PUT "/" {body :body} body)
                               (DELETE "/" [] id)
                               ))
                    )
           (context "/admin" []
                    (GET "/" [] (str []))
                    (POST "/" {body :body} body)
                    (context "/:id" [id]
                             (GET "/" [] id)
                             (PUT "/" {body :body} body)
                             (DELETE "/" [] id)
                             )
                    ))
  (not-found "unexists path"))

(defroutes old
  (POST "/login" request login)
  (POST "/logout" request logout)

  (POST "/register" {params :params} (register params))

  (POST "/addevent" {params :params} (init-event params))

  (POST "/moneyout" {{money :money username :target} :params} (moneyout money username))

  (GET "/start" {params :params} (start params))
  (GET "/participate" {params :params} (participate params))
  (GET "/pay" {params :params} (pay params))

  ; Like REST API
  (GET "/affirm" {{fid :fid} :params} (affirm fid))
  (GET "/refute" {{fid :fid} :params} (refute fid))

  ; Just test tool
  (ANY "/api/pong/:id" [params] (fn [{p :params}] (pprint p)))


  ;;(GET "/api/help" [_] (api/help))
  ;;(GET "/api/events" {{types :types} :params} (api/get-events types))


  ;(resources "/") ;Should be after pages. Search all css, js, etc. in dir 'resources' in root of project
  ) ;params should be last, it overlap all below routes.

(def engine
  (-> api
    ;(wrap-routes wrap-stacktrace {:color? true})
    (wrap-routes wrap-json-response)
    (wrap-routes wrap-content-json)
	(wrap-routes #(wrap-json-body % {:keywords? true :bigdecimals? true}))
    ;(wrap-routes wrap-json-params) ;; read body as JSON in json-params
    (wrap-routes wrap-nested-params) ;; for such foo[bar]=e => {:foo {:bar e}}
    (wrap-routes wrap-keyword-params) ;; should be first
    (wrap-routes wrap-params) ;; add query params to :params(json-params)
    ))
