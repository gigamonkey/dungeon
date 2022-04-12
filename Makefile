sources := $(wildcard src/com/gigamonkeys/dungeon/*.java)
main := com.gigamonkeys.dungeon.Dungeon
resources :=

all: build

build: compile resources

compile: $(sources)
	javac -Xdiags:verbose -Xlint:deprecation -cp src:classes -d classes/ $(sources)

pretty:
	prettier --plugin ~/node_modules/prettier-plugin-java/ --write **/*.java

resources: $(resources)

run:
	java -cp classes $(main)

clean:
	rm -rf classes
