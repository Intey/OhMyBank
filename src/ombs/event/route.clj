(ns ombs.event.route
  (:require
    [compojure.api.sweet :refer :all]
    [ring.util.http-response :refer :all]
	[schema.core :as s]
    [ombs.event.handler :as apie]
    [ombs.handler.api.users :as apiu]
    ))

(s/defschema InEvent
  {:author s/Str
   :name s/Str
   :date s/Str
   :price s/Num
   (s/optional-key :participants) (describe [apiu/User]
                                            "Usernames of paticipats."
                                            :type s/Str) })

(defroutes* events
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
        nil}] ;; optional enul

      (ok (apie/get-events types)))


    (POST* "/" []
      :summary "Add event or events."
      :body-params

      [events :- (describe [InEvent] "Event data")]
      (ok (map apie/new-event events)))


    (context* "/:id" []


      (GET* "/" [id]
        :path-params [id :- s/Int]
        :summary "Return event by id with participants"

        (apie/get-event id))


      (PUT* "/" []
        :summary "Change some event. Return New event if change applies return."
        :body-params
        [name :- (describe s/Str "New name")
         date :- (describe s/Str "New date")
         price :- (describe s/Num "New price") ]

        (ok {}))


      (DELETE* "/" [id]
        :summary "Remove event. return  Null, if all went's good."
        (ok {}))


      (context* "/participants" []
        :tags ["participants"]


        (GET* "/" [id]
          :summary "Return list of participants of event"
          (apie/participants id))


        (POST* "/" [id]
          :summary
          (str "add new participant to event. Note, that this initiate many "
               "recalc of debts.")
          :body-params [username :- s/Str]

          (apie/participate id username))

                ) ;; participation context
      ) ;; event context
    )) ;; events context


