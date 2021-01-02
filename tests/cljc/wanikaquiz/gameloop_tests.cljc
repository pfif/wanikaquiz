(ns wanikaquiz.gameloop-tests
  (:require
   [clojure.test :refer :all]
   [wanikaquiz.gameloop :as gameloop]))

(deftest testing-ci (testing "testing-ci"
                      (is (= (gameloop/return-false) true))))
