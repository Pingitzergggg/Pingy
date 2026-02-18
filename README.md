# Pingy
 
---
### THE programming language
---
```js 
print "Hello, World!";
```

#### Variables
```js
var @dataType variableName = value;
```
- Variables are declared with the `var` statement, followed by a datatype and variable name
- Numeric variables will get a default value of 0 and booleans false, all other types must be given a value when declaring


---

#### Data type examples

###### string
```js
var @string varname = "meow";
```

###### byte
```js
var @byte varname = 127; // max
var @byte varname = -128; // min
```

###### short
```js
var @short varname = 32767; // max
var @short varname = -32768; // min
```

###### int
```js
var @int varname = 2147483647; // max
var @int varname = -2147483648; // min
```

###### long
```js
var @long varname = 9223372036854775807; // max
var @long varname = -9223372036854775808; // min
```

###### float
```js
var @float varname = 1.0;
```

###### double
```js
var @double varname = 1.000000001;
```

###### bool
```js
var @bool varname = true;
var @bool varname = false;
// value cannot be an expression
```
---

#### Expressions
###### arithmetic
```js
add + me;
subtract - me;
multiply * me;
divide / me;
-negateMe;
```
###### comparison and equality
- these expression will result in a bool value.
```js
less < than;
lessThan <= orEqual;
greater > than;
greaterThan >= orEqual;

equal == equal; // true
equal != equal; // false
```
###### logical operators
```js
!true; // false
!false; // true
```
###### && (and)
|A|B|&&|
|-|-|-|
|T|T|T|
|T|F|F|
|F|T|F|
|F|F|F|
###### || (or)
|A|B|\|\||
|-|-|-|
|T|T|T|
|T|F|T|
|F|T|T|
|F|F|F|
###### ^^ (xor)
|A|B|^^|
|-|-|-|
|T|T|F|
|T|F|T|
|F|T|T|
|F|F|F|
---
#### Statements
###### print
- The print statement evaluates a single expression and displays the result
```js
print "Hello, World!"; // Hello, World!
print 1 + 2; // 3
```
###### if
- An if statement executes one of two statements based on some condition
- there is no else if
```js
if statement {
    // true branch
} else {
    // false branch
}
```

