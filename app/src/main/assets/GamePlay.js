<html>
    <body>
        <script type="text/javascript">
            var _ic = _ic || [];
            _ic.push(['server', Mobile.getURL()]);
            _ic.push(['gametype', Mobile.getGameType()]);
            _ic.push(['player_id', Mobile.getPlayerId()]);
            _ic.push(['player_name', Mobile.getPlayerName()]);
            _ic.push(['session_id', Mobile.getSessionId()]);
            _ic.push(['balance', Mobile.getBalance()]);
            _ic.push(['language', Mobile.getLanguage()]);
            _ic.push(['currency', Mobile.getCurrency()]);
            _ic.push(['alias', 'ice.igamew.com']);
            _ic.push(['iframe_div_id', 'lottogames_div_iframe']);
            _ic.push(['isMobileApp', '1']);
            (function () {
                document.write('<' + 'script type="text/javascript" src="'+Mobile.getURL()+'assets/js/lottogames.js?"><'+'/script>')
            })();

            function callJS() {
                LottoGames.frame(_ic);
            }

        </script>
    </body>
</html>