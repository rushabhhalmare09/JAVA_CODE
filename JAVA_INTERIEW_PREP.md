Core Java: -
This page focuses on oddities of Core Java from an interview perspective. For basic topics like inheritance, interfaces, etc. please read the book mentioned below.

Default init values: - 
   For fields (class level variables), values are auto assigned default values.
   Method local variables should be manually assigned.
   Default values (references = null, primitives = 0, boolean = false)
   Arrays initialize its elements: int[] numbers = new int[10]; will assign all ints in the array to 0.

String pool: -
  String constants are placed in a memory pool
  When retrieved, returns reference to string in the pool.
  Pool saves memory. New string constants with same value share same instance in the pool.
  String is immutable thus these values are never changed. For any updates, new string constant is created.
  String s = "abc" will place "abc" in pool and return its reference.
  String s = new String("abc") will also place "abc" in pool, as well as allocate new memory.

   JVM Internals: -
     Some of the topics listed below are tricks JVM uses to improve performance. Subset of these can be exploited to further improve application performance.
       Note: These topics are highly unlikely to come up in an interview. Feel free to just glance through without digging deep.
     Compressed pointers: -
        32 bit references can address 4GB, while 64 bit can reference 2^64 bytes (though limited by OS/RAM on the machine).
        Having 64 bit reference for every object increases memory usage. JVMs use compressed pointers to address this issue. (For more details: -> https://wiki.openjdk.java.net/display/HotSpot/CompressedOops)
        Basic idea is to store 32-bits per reference and then add to a base address to find final 64-bit address.
        Flag: -XX:+UseCompressedOops. Latest versions of 64-bit Java have this argument by default.
        Addresses upto 4gb untranslated.
        Addresses 4gb to 28gb, remove 3bits, because Java has 8 byte word aligned, thus 3 bits need not be stored.
        Important because Java has more references. In C++ memory layout follows struct layout.
        Java 8 has JVM args, +XX:ObjectAlignmentInBytes=16 for heap between 32gb and 64gb.  
  
  
