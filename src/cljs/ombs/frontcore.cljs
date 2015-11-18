(ns ombs.frontcore
  (:require
    [kioo.om :refer [content set-attr do-> substitute listen]]
    [kioo.core :refer [handle-wrapper]]
	[om.core :as om :include-macros true]
	[om-tools.dom :as dom :include-macros true]
	)
  (:require-macros [kioo.om :refer [defsnippet deftemplate]]))

(defsnippet nav-component "public/test.html" [:.nav-item]
  [[caption func]]
  {[:a] (do-> (content caption)
              (listen :onClick #(func caption)))})

(defsnippet header "public/test.html" [:header]
  [{:keys [heading navigation]}]
  { [:h1] (content heading)
    [:ul] (content map nav-component navigation)
   })

(deftemplate my-page "public/test.html"
  [data]
  { [:header] (substitute (header data))
   [:.content] (content (:content data)) })

(defn init [data] (om/component (my-page data)))

(def app-state {:header "main"
                :content "Hello world"
                :navigation [["home" #(js/alert %)]
                             ["next" #(js/alert %)]] })

(om/root init app-state {:target (.-body js/document)})
