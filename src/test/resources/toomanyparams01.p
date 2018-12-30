// Simple RUN cases, no issue here
run xxx.p.
run xxx.p (p1, p2).
// Simple RUN cases, issue has to be found here
run xxx.p (p1, p2, p3, p4, p5).
run value("xxx.p") in h persistent set h2 (p1, p2, p3, p4, p5).

// Simple dynfunc case, no issue
dynamic-function("FuncName").
dynamic-function("FuncName", p1, p2, p3, p4).
// Simple dynfunc case
dynamic-function("FuncName", p1, p2, p3, p4, p5).

// Simple PUBLISH cases, no issue here
publish "xxx".
publish "xxx" (p1, p2).
// Simple RUN cases, issue has to be found here
publish "xxx" (p1, p2, p3, p4, p5).

// Nested call, two issues have to be raised
run xxx.p (p1, p2,
    dynamic-function("FuncName", p1, p2, p3, p4, p5), p3, p4, p5).

// Static method call
Progress.Lang.FooBar:UnknownStaticCall(p1, p2, p3, p4).
Progress.Lang.FooBar:UnknownStaticCall(p1, p2, p3, p4, p5).

// Function declaration shouldn't raise issue
function f1 returns integer (p1 as int, p2 as int, p3 as int, p4 as int, p5 as int):

end function.

// Ignore issue
{&_proparse_ prolint-nowarn(num_parameters)}
run xxx.p (p1, p2,
    dynamic-function("FuncName", p1, p2, p3, p4, p5), p3, p4, p5).
