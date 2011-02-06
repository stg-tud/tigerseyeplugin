syntax(
[
rule(
math,
expression(
[
termFromFactors(
[
factorFromExpressionInBraces(
expression)
] as Factor[])
] as Term[])),
rule(
expression,
expression(
[
plus,
minus
] as Term[])),
rule(
plus,
expression(
[
termFromFactors(
[
number,
factorFromQuotedSymbol(
quotedSymbolFromAnyCharacters(
[
new AnyCharacter("+")
] as AnyCharacter[]))
] as Factor[])
] as Term[])),
number,
rule(
minus,
expression(
[
termFromFactors(
[
number,
factorFromQuotedSymbol(
quotedSymbolFromAnyCharacters(
[
new AnyCharacter("-")
] as AnyCharacter[]))
] as Factor[])
] as Term[])),
number,
rule(
number,
digit)
] as Rule[]) { syntax(
[
digit
] as Rule[]) }