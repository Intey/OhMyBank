(ns ombs.views
  (:require [net.cgrand.enlive-html :as h]))

(h/deftemplate page-index "ombs/index.html" [ctxt]
               [:title]  (h/content "Awesome application")
               [:#error] (h/set-attr "style" (str "display:" (if (:error ctxt) "inline" "none")))
               [:#error] (h/content ( if (:error ctxt) (:error ctxt) "" ))) 

