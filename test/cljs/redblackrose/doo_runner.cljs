(ns redblackrose.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [redblackrose.core-test]))

(doo-tests 'redblackrose.core-test)

