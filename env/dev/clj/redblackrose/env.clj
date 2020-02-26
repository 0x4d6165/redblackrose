(ns redblackrose.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [redblackrose.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[redblackrose started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[redblackrose has shut down successfully]=-"))
   :middleware wrap-dev})
