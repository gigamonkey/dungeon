sources := $(shell find -name '*.java')
main := com.gigamonkeys.dungeon.Dungeon
resources :=

golden_sha = cat golden.txt | shasum | cut -c 1-40
current_sha = ./run run.txt | shasum | cut -c 1-40

all: build

build: compile resources

compile: $(sources)
	javac -Xdiags:verbose -Xlint:deprecation -Xlint:unchecked --enable-preview --source 17 -cp src:classes -d classes/ $(sources)

pretty:
	prettier --plugin ~/node_modules/prettier-plugin-java/ --write **/*.java

resources: $(resources)

run:
	java --enable-preview --source 17 -cp classes $(main)

test:
	java -cp classes com.gigamonkeys.dungeon.Test

clean:
	rm -rf classes

tidy:
	find . -name '*~' -delete

check:
	@if [ "`$(current_sha)`" == "`$(golden_sha)`" ]; then echo Good; else echo Bad; fi

golden.txt:
	./run run.txt > $@

candidate.txt:
	./run run.txt > $@
