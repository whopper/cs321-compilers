#! /u/hopperw/.rvm/rubies/ruby-2.1.0/bin/ruby

dir = '/u/hopperw/PortlandState/fall2014/compilers/homework/project2/tst/error'

Dir.foreach(dir) do |item|
  next if item == '.' or item == '..'

  puts "Test: #{item}\n"
  system("java mjRawGrammar #{dir}/#{item}")
end
