(ns ombs.route
  (:require
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.nested-params :refer [wrap-nested-params]]

    [compojure.api.sweet :refer :all]
    [ring.util.http-response :refer :all]
	[schema.core :as s]
    [clojure.pprint :refer [pprint]]

    [ombs.handler.api.events :as apie]
    [ombs.handler.api.users :as apiu]

    ))

;;(s/defschema Date org.joda.time.LocalDate )
(s/defschema InEvent
  {:author s/Str
   :name s/Str
   :date s/Str
   :price s/Num
   (s/optional-key :participants) (describe [apiu/User] "Usernames of paticipats.") })

(defapi engine
  (swagger-ui)
  (swagger-docs
    {:info {:title "Bank API" :description "API for bank" }
     :tags ["api" "event" "user"] })

  (context* "/api" []
    :tags ["api"]

    (context* "/events" []
      :tags ["events"]


      (GET* "/" []
        :summary "Return events list with participants list for each"
        :query-params
        [{types :-
          (describe
            [(s/enum :initial :finished :in-progress)]
            (str "Select events by types. Optional."
                 "Use query string: /api/events?types=initial&types=finished."))
          nil}]

        (ok (apie/get-events types) ))


      (POST* "/" []
        :summary "Add event or events."
        :body-params
        [events :- (describe [InEvent] "Event data")]

        (ok (map apie/new-event events))
        )


      (context* "/:id" []


         (GET* "/" [id]
           :path-params [id :- s/Int ]
           :summary "Return event by id with participants"
           (apie/get-event id))


         (PUT* "/" []
           :summary "Change some event. Return New event if change applies return."
           :body-params
           [name :- (describe s/Str "New name")
            date :- (describe s/Str "New date")
            price :- (describe s/Num "New price") ]

           (ok {}))


         (DELETE* "/" []
           :summary "Remove event. return  Null, if all went's good."
           (ok {}))


         (context* "/participants" []
           :tags ["participants"]


           (GET* "/" []
                 :summary "Return list of participants of event"
                 (ok {}))


           (POST* "/" []
             :summary
             (str "add new participant to event. Note, that this initiate many "
                  "recalc of debts.")
             :body-params [unsername :- (describe apiu/User "Registered username")]

             (ok {}))
           ) ;; participation context
         ) ;; event context
      ) ;; events context


    (context* "/users" []
              :tags [ "user"]


              (GET* "/" []
                    :summary "Return list of users")


              (POST* "/" []
                     :summary "Add new clients to bank. After this, they can participate, create events etc."
                     :body-params [{users :-
                                    (describe [apiu/User] "Usernames of paticipats.")
                                    nil}]

                     (ok (map apiu/add-user users))
                     ))

          ))
;; (-> engine
;;     (wrap-routes wrap-nested-params)
;;     (wrap-routes wrap-params))
