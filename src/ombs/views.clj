(ns ombs.views
  (:require [net.cgrand.enlive-html :as h]))

(h/deftemplate page-index "ombs/index.html" [ctxt]
             [:title] (h/content "Awesome application")
             [:#old]  (h/content (:old ctxt))
             [:#msg2] (h/set-attr "style" "display: none"))

(h/deftemplate page-summary "ombs/index.html" [ctxt]
             [:title] (h/content "Awesome application")
             [:#old]  (h/content (:old ctxt))
             [:#msg2] (h/content  (if (:error ctxt) (:error ctxt)
                                  (str "Summary is " (:sum ctxt)))))

; (deftemplate new-event "ombs/newevent.html" [])
