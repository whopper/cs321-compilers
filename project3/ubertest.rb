#! /u/hopperw/.rvm/rubies/ruby-2.1.0/bin/ruby

(1..36).each do |num|
  num = "0" + num.to_s if num.between?(1,9)
  puts "Running: diff tst/test#{num}.ast.ref tst/out/test#{num}"
  system "diff tst/test#{num}.ast.ref tst/out/test#{num}"
#  puts "Running: java mjParser tst/test#{num}.java > tst/out/test#{num}"
#  system "java mjParser tst/test#{num}.java > tst/out/test#{num}"
end
