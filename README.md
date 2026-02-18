# Pingy
 
---
### THE programming language
---
```py
print "Hello, World!";
```

#### Variables
```ts
var @dataType variableName = value;
```
- Variables are declared with the `var` key word, followed by a datatype and variable name
- Numeric variables will get a default value of 0, booleans false, and strings an empty string 
- Variable names cannot start with a number or match any statement names

#### Basic syntax
- datatypes are detected by the `@` character
- scopes are defined within curly brackets `{` `}`
- expressions must end with a semi-colon `;`

---

#### Data type examples

###### string
```ts
var @string varname = "meow";
```

###### byte
```ts
var @byte varname = 127; // max
var @byte varname = -128; // min
```

###### short
```ts
var @short varname = 32767; // max
var @short varname = -32768; // min
```

###### int
```ts
var @int varname = 2147483647; // max
var @int varname = -2147483648; // min
```

###### long
```ts
var @long varname = 9223372036854775807; // max
var @long varname = -9223372036854775808; // min
```

###### float
```ts
var @float varname = 1.0;
```

###### double
```ts
var @double varname = 1.000000001;
```

###### bool
```ts
var @bool varname = true;
var @bool varname = false;
// value cannot be an expression
```
---

#### Expressions
###### arithmetic
```ts
add + me;
subtract - me;
multiply * me;
divide / me;
-negateMe;
```
###### comparison and equality
- these expression will result in a bool value.
```ts
less < than;
lessThan <= orEqual;
greater > than;
greaterThan >= orEqual;

equal == equal; // true
equal != equal; // false
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
- The `print` statement evaluates a single expression and displays the result
```py
print "Hello, World!";
print 1 + 2;
```
```
Hello, World!
3
```
###### if
- An `if` statement executes one of two statements based on some condition
```ts
if statement {
    // true branch
} else {
    // false branch
}
```
- You can also use `elif` to create multiple branches with different statements
```py
if statement {

} elif statement2 {

} else {

}
```
###### while
- A `while` loop executes the body repeatedly as long as the condition is true
```ts
while expression {
    // body
}
```
###### loop
- The `loop` loop executes the body repeatedly. 
- Depending on the parameters given, it can function in different ways

examples:
```rust
loop 5 {
    // this will run 5 times
}
```
- When a numeric value is given as a parameter it executes the body `parameter` times. In this case it runs down 5 times.
```rust
loop expression {
    // this will run while the expression is true
}
```
- If the parameter is a expression that results in a `bool` type, the loop acts like a `while` loop and executes the body repeatedly *while* that parameter expression is true
```rust
loop i < 5 with i {
    // this will run 5 times
}
```
- You can give the `loop` a cycle variable using the `with` keyword
- The cycle variable is 0 by default
```rust
loop i < 6 with i by 2 {
    // this will run 3 times
}
```
- Every cycle, this variable increases by 1, but using the `by` keyword lets you change how much to increment it by
```rust
loop 3..5 with i {
    // this will run 2 times
}
```
- `startingValue..endValue` and `startingValue..=endValue` lets you define the cycle variable's starting value
- This loop with run until the cycle variable's value reaches the `endValue`
```rust
loop i < 0 with i check after {
    // this will run once
}
```
```rust
loop i < 0 with i check before {
    // this will not run
}
```
- The `check before/after` keyword makes the cycle evaluate the condition either before or after the cycle runs
- `check after` ensures that the cycle runs down at least once

you can also mix the keywords
```rust
loop 8..=100 with i by 5 check after {
    // body
}
```