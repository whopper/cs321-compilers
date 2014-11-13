#! /u/hopperw/.rvm/rubies/ruby-2.1.0/bin/ruby

(1..35).each do |num|
  system "java mjParser tst/test#{num}.java > tst/out/test#{num}"

end
