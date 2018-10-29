include $(top_srcdir)/progstm32.mk

.PHONY: app

all: app

# Build java application
app:
	$(call pry,"Building application...............")
	ant clean
	ant app -Dspcore=$(extlib_spcore) -Dsptty=$(extlib_sptty) -Duartsdk=$(progstm32uart_jar) -Dappjarname=$(progstm32app_jar) -Dextlibpath=$(top_srcdir)/build

# Install progstm32 app, jars and manpage considerig the --prefix option
# supplied by user to configure script. If the user does not gives --prefix option
# default which is typically /usr/local will be used.
install-exec-hook:
	$(call pry,"Installing progstm32 application...")
	${MKDIR_P} $(bindir)
	cp $(top_srcdir)/application/progstm32 $(bindir)
	chmod 0755 $(bindir)/progstm32
	${MKDIR_P} $(datarootdir)/progstm32
	cp $(top_srcdir)/build/$(progstm32app_jar) $(datarootdir)/progstm32
	cp $(top_srcdir)/build/$(progstm32uart_jar) $(datarootdir)/progstm32
	cp $(top_srcdir)/build/$(extlib_spcore) $(datarootdir)/progstm32
	cp $(top_srcdir)/build/$(extlib_sptty) $(datarootdir)/progstm32
	chmod 0755 $(datarootdir)/progstm32/$(progstm32app_jar)
	chmod 0755 $(datarootdir)/progstm32/$(progstm32uart_jar)
	chmod 0755 $(datarootdir)/progstm32/$(extlib_spcore)
	chmod 0755 $(datarootdir)/progstm32/$(extlib_sptty)
	${MKDIR_P} $(mandir)/man1
	cp $(top_srcdir)/application/progstm32.1 $(mandir)/man1
	chmod 0755 $(mandir)/man1/progstm32.1

# Remove the files installed from file system
uninstall-local:
	rm $(bindir)/progstm32
	rm $(datarootdir)/progstm32/$(progstm32app_jar)
	rm $(datarootdir)/progstm32/$(progstm32uart_jar)
	rm $(datarootdir)/progstm32/$(extlib_spcore)
	rm $(datarootdir)/progstm32/$(extlib_sptty)
	rm $(mandir)/man1/progstm32.1

# Let ant tool clean the generates class files
clean-local:
	ant clean
	[ -n "$(top_srcdir)" ] && rm -rf $(top_srcdir)/build/$(progstm32app_jar)
