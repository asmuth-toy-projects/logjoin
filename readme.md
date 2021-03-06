kollekt
=======

kollekt listens on a udp socket and collects bits of data in buckets (grouped by a session/bucket-id). 
the buckets are kept in ram and eventually flushed to disk when one of these conditions was met:
  
  + no appends to the bucket for N seconds (bucket_idle_timeout)
  + or: the maximum bucket size is reached (bucket_maxsize)
  + or: the maximum bucket lifetime is reached (bucket_maxage)


Usage
-----

    kollekt [-lpxh] [options] /path/to/out_dir

      -l, --listen
        listen on udp for tuples on this address

      -p, --port
        listen on udp for tuples on this address

      -x, --keep-deadlist
        keep a list of killed buckets in mem (ensure bucket uniqueness)

      -s, --file-size
        set the output file 'length' to N seconds (default: 1hour)

      --bucket-timeout
        flush buckets to disk after N seconds of inactivity (default: 2hours)

      --bucket-maxsize
        flush buckets to disk when they reach N items (default: 1024)

      --bucket-maxage
        flush buckets to disk after at most N seconds (default: 1day)

      -h, --help
        print this message




Input Format
------------

the input is a stream of `(bucket_id, data)` tuples. every tuple should be sent via udp as an utf-8 encoded string.

the message/tuple format is:
    
    bucket_id;data

example messages/tuples (one per line):

    session123;keyword1
    session123;keyword2
    session456;keyword5
    session123;keyword3
    session456;keyword1
    ...



Output Format
-------------

all "closed" buckets are written to per-timespan files. the default file length is 30 minutes. the created files will look like this:

    ./dump/1331597057.csv
    ./dump/1331598459.csv
    ./dump/1331510059.csv

the format of the csv files is 

    time_of_dump;bucket_id;data1;data2;data3;data4;(...);dataN

example:

    $ cat ./dump/1331597057.csv
    1331597057.0012;session123;keyword1;keyword2
    1331597057.0843;session751;keyword4;keyword1;keyword3
    1331597057.1274;session542;keyword2
    1331597057.2427;session642;keyword3;keyword2
    ...



License
-------

Copyright (c) 2011 Paul Asmuth

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to use, copy and modify copies of the Software, subject 
to the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
