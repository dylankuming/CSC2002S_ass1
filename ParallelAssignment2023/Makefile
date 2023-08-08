JAVAC = C:\Program Files\Java\jdk-19\bin\javac
JAVA = C:\Program Files\Java\jdk-19\bin\java
.SUFFIXES: .java .class
SRCDIR = src/MonteCarloMini
BINDIR = bin/MonteCarloMini

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) -sourcepath $(SRCDIR) $<

CLASSES= TerrainArea.class SearchParallel.class MonteCarloMinimizationParallel.class
CLASS_FILES = $(CLASSES:%.class=$(BINDIR)/%.class)
JAVA_FILES = $(CLASSES:%.class=$(SRCDIR)/%.java)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/*.class

run:
	java -cp bin MonteCarloMinimizationParallel