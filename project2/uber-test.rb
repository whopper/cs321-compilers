#! /u/hopperw/.rvm/rubies/ruby-2.1.0/bin/ruby

Dir.foreach('./tst') do |item|
  next if item == '.' or item == '..'

  puts "Test: #{item}\n"
  system("java mjRawGrammar ./tst/#{item}")
end
