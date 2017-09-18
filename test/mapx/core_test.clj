(ns mapx.core-test
  (:require [clojure.test :refer :all]
            [mapx.core :refer :all]))

(def in {:a 1 :b 2 :c 3 :d 4 :e 5})

(deftest test-or
  (is (= {:a 1 :b 2 :c 3 :d 4 :e 5 :f 7}
         (transform in :or {:a 123, :f 7}))))

(deftest test-select
  (is (= {:a 1 :c 3}
         (transform in :select [:a :c]))))

(deftest test-delete
  (is (= {:a 1 :b 2 :e 5}
         (transform in :delete [:c :d]))))

(deftest test-update
  (testing "simple case"
    (is (= {:a 1 :b -2 :c 3 :d 40 :e 5}
           (transform in :update {:b -, :d (partial * 10)}))))
  (testing "missing key"
    (is (= {:a 1 :b 2 :c 3 :d 40 :e 5}
           (transform in :update {:z -, :d (partial * 10)})))))

(deftest test-rename
  (is (= {:x 1 :y 2 :z 3 :d 4 :e 5}
         (transform in :rename {:a :x, :b :y, :c :z}))))

(deftest test-all-together
  (is (= {:x 50 :z -3}
         (transform in
                    :select [:a :c :e]
                    :delete [:e]
                    :update {:a (partial * 50), :c -}
                    :rename {:a :x, :c :z}))))

(deftest test-projection
  (is (= {:x 1 :z 3}
         (transform in
                    :project {:a :x, :c :z}))))

(deftest test-all-together-with-projection
  (is (= {:x 50 :z -3}
         (transform in
                    :update {:a (partial * 50), :c -}
                    :project {:a :x, :c :z}))))
